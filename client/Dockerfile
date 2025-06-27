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
RUN yarn build

# ---- Runtime Stage ----
# Use an Nginx base image and install Node.js
FROM nginx:1.25-alpine
RUN apk add --no-cache nodejs
WORKDIR /app

# Copy the built Nuxt app from the builder stage
COPY --from=builder /app/.output /app/.output


# Copy the Nginx configuration and the entrypoint script
COPY nginx.conf.template /etc/nginx/conf.d/default.conf.template
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Set the internal port for the Nuxt server
ENV PORT=3000
EXPOSE 80

# Run the entrypoint script
CMD ["/app/entrypoint.sh"]
