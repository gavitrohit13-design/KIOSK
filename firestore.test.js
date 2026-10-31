const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-printflo-ffdc38a1-62ba-41af-83c4-e18d317a8f24";

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8085,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read orders", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore({ databaseId: DATABASE_ID });
  await assertFails(unauthedDb.collection("orders").doc("order_1").get());
});

test("Customer can create and read their own order", async () => {
  const customerDb = testEnv.authenticatedContext("cust_123").firestore({ databaseId: DATABASE_ID });
  const orderRef = customerDb.collection("orders").doc("ORD-001");

  await assertSucceeds(
    orderRef.set({
      userId: "cust_123",
      customerName: "John Doe",
      customerPhone: "+919876543210",
      fileName: "invoice.pdf",
      fileType: "PDF",
      fileSize: 102400,
      pageCount: 5,
      copies: 2,
      colorMode: "BW",
      duplexMode: "SINGLE",
      paperSize: "A4",
      pageRange: "ALL",
      orientation: "PORTRAIT",
      scaleMode: "FIT_TO_PAGE",
      printQuality: "NORMAL",
      subtotal: 100,
      gstAmount: 18,
      serviceCharge: 2,
      discountAmount: 0,
      totalAmount: 120,
      paymentMethod: "UPI",
      paymentStatus: "PENDING",
      orderStatus: "PENDING",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  await assertSucceeds(orderRef.get());
});

test("Customer cannot read other customer's order", async () => {
  // Setup order with admin/context
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore({ databaseId: DATABASE_ID });
    await adminDb.collection("orders").doc("ORD-SECRET").set({
      userId: "user_a",
      fileName: "confidential.pdf",
      pageCount: 1,
      copies: 1,
      colorMode: "BW",
      duplexMode: "SINGLE",
      paperSize: "A4",
      totalAmount: 10,
      paymentStatus: "PAID",
      orderStatus: "COMPLETED",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const otherCustomerDb = testEnv.authenticatedContext("user_b").firestore({ databaseId: DATABASE_ID });
  await assertFails(otherCustomerDb.collection("orders").doc("ORD-SECRET").get());
});

test("Shop settings can be read by authenticated users and updated by owner", async () => {
  const ownerDb = testEnv.authenticatedContext("owner_999").firestore({ databaseId: DATABASE_ID });
  const shopRef = ownerDb.collection("shops").doc("main_shop");

  await assertSucceeds(
    shopRef.set({
      ownerId: "owner_999",
      shopName: "RPG AUTO PRINT",
      tagline: "Scan • Upload • Pay • Print",
      bwPricePerPage: 10,
      colorPricePerPage: 20,
      gstPercentage: 18,
      discountPercentage: 0,
      serviceCharge: 2,
      minOrderAmount: 10,
      autoPrintEnabled: true,
      language: "en",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  // Another user can read shop settings
  const customerDb = testEnv.authenticatedContext("cust_123").firestore({ databaseId: DATABASE_ID });
  await assertSucceeds(customerDb.collection("shops").doc("main_shop").get());

  // But customer cannot update shop settings
  await assertFails(
    customerDb.collection("shops").doc("main_shop").update({
      bwPricePerPage: 1,
      updatedAt: new Date()
    })
  );
});
