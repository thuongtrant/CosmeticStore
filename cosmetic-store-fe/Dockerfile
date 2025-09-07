# Build stage
FROM --platform=$BUILDPLATFORM node:alpine AS build

# Set working directory
WORKDIR /app

# Copy package files first (better layer caching)
COPY package*.json ./

# Install dependencies with legacy peer deps to handle React 19
# Use npm ci for faster, reliable builds in production
RUN npm ci --legacy-peer-deps --only=production --silent

# Copy environment file
COPY .env ./

# Copy source code (separate layer to leverage cache)
COPY public/ ./public/
COPY src/ ./src/
COPY *.js *.json *.md ./

# Build the app
RUN npm run build

# Production stage - use smaller nginx image
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built assets from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]