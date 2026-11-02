# Web MoSeguros: build com Node + pnpm, servida por nginx sem root.
# Imagens fixadas e listadas em scripts/images.txt.

# --- build ---
FROM node:22-alpine AS build
WORKDIR /app
ENV COREPACK_ENABLE_DOWNLOAD_PROMPT=0
RUN corepack enable

COPY package.json pnpm-lock.yaml ./
RUN --mount=type=cache,target=/root/.local/share/pnpm/store \
    pnpm install --frozen-lockfile

COPY . .
# VITE_API_BASE_URL vazio: a API é pedida na mesma origem e o nginx faz o proxy de /api.
RUN pnpm build

# --- runtime ---
FROM nginxinc/nginx-unprivileged:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

# A imagem já corre como o utilizador nginx (uid 101) e escuta na 8080.
USER 101
EXPOSE 8080
