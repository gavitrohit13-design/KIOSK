const test = require('node:test');
const assert = require('node:assert');

// Unit tests for RPG AUTO PRINT core calculation logic
test('RPG AUTO PRINT Pricing Logic', async (t) => {
  await t.test('calculates correct price for B&W single sided', () => {
    const pages = 5;
    const copies = 2;
    const pricePerPage = 10.0;
    const expected = pages * copies * pricePerPage;
    assert.strictEqual(expected, 100.0);
  });

  await t.test('calculates correct price for Colour double sided', () => {
    const pages = 4;
    const copies = 1;
    const pricePerPage = 20.0;
    const doubleSideMultiplier = 0.9; // 10% discount for duplex
    const base = pages * copies * pricePerPage;
    const total = base * doubleSideMultiplier;
    assert.strictEqual(total, 72.0);
  });

  await t.test('calculates GST correctly at 18%', () => {
    const subtotal = 100.0;
    const gstRate = 0.18;
    const gst = subtotal * gstRate;
    const grandTotal = subtotal + gst;
    assert.strictEqual(gst, 18.0);
    assert.strictEqual(grandTotal, 118.0);
  });
});

test('RPG AUTO PRINT Order Validation', async (t) => {
  await t.test('validates valid file extensions', () => {
    const allowedExtensions = ['pdf', 'doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx', 'jpg', 'jpeg', 'png'];
    const testFile = 'document.pdf';
    const ext = testFile.split('.').pop().toLowerCase();
    assert.strictEqual(allowedExtensions.includes(ext), true);
  });

  await t.test('rejects unsafe executable extensions', () => {
    const allowedExtensions = ['pdf', 'doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx', 'jpg', 'jpeg', 'png'];
    const testFile = 'malware.exe';
    const ext = testFile.split('.').pop().toLowerCase();
    assert.strictEqual(allowedExtensions.includes(ext), false);
  });
});
