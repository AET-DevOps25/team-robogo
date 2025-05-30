# ---- Build Stage ----
FROM node:18 AS builder

LABEL maintainer="Team Robogo"
LABEL version="1.0"
LABEL description="Team Robogo Frontend Application"
LABEL org.opencontainers.image.source="https://github.com/AET-DevOps25/team-robogo"

WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# ---- Runtime Stage ----
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
