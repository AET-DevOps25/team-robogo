# ---- Build Stage ----
# Build the Nuxt application
FROM node:20-slim AS builder
WORKDIR /app
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile
COPY . .
# The API base is set to a relative path for production proxying
ARG NUXT_PUBLIC_API_BASE=/
ENV NUXT_PUBLIC_API_BASE=$NUXT_PUBLIC_API_BASE
# Clean any existing build artifacts and rebuild
RUN rm -rf .nuxt .output node_modules/.cache
RUN yarn build

# ---- Runtime Stage ----
FROM node:20-slim
WORKDIR /app

# Copy the entire app directory from the builder stage to ensure .output is included
COPY --from=builder /app .

# Set the internal port for the Nuxt server
ENV PORT=3000
ENV HOST=0.0.0.0

# Expose the port
EXPOSE 3000

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/ || exit 1

# Run the server
CMD ["node", ".output/server/index.mjs"]
