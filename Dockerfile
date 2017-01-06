FROM clojure:alpine
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app
RUN lein deps
COPY . /usr/src/app
RUN lein migrate
ENV PORT=80
CMD ["lein", "run"]