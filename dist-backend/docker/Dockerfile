# =========================================================================
# RPG AUTO PRINT - Production Dockerfile
# =========================================================================

# Stage 1: Build Frontend Web Application (React + Vite + Tailwind + PWA)
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY package*.json ./
RUN npm ci --prefer-offline --no-audit
COPY . .
RUN npm run build || true

# Stage 2: Build Node.js Backend Server
FROM node:20-alpine AS backend
WORKDIR /app
ENV NODE_ENV=production
COPY package*.json ./
RUN npm ci --only=production --prefer-offline --no-audit
COPY . .

# Copy built frontend assets to public directory
COPY --from=frontend-builder /app/frontend/dist ./public

# Install CUPS & PDF printing utilities for Linux printer spooling
RUN apk add --no-cache cups-client ghostscript poppler-utils

EXPOSE 3000
CMD ["node", "server.js"]
