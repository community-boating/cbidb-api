FROM openjdk:11
RUN mkdir /app
COPY "CBI DB API_0.1.0_all.deb" /app/
WORKDIR /app
RUN dpkg -i "CBI DB API_0.1.0_all.deb"
