FROM theasp/clojurescript-nodejs:latest as build
WORKDIR /app
COPY project.clj .
RUN lein do deps
RUN npm install
COPY . .
RUN lein prod


FROM nginx:alpine
COPY --from=build /app/resources/* /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
