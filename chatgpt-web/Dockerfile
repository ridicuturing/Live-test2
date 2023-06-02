FROM nginx:mainline-alpine-slim

#COPY /service /app
COPY ./dist /usr/share/nginx/html

WORKDIR /app
RUN echo 'server {\
    listen 80;\
    server_name _;\
    location / {\
      root /usr/share/nginx/html;\
      index index.html;\
    }\
    location /api {\
      proxy_pass http://localhost:8080/api;\
      proxy_set_header Host $host;\
      proxy_set_header X-Real-IP $remote_addr;\
      proxy_set_header X-Forwarded-Proto $scheme;\
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\
    }\
}' > /etc/nginx/conf.d/default.conf


