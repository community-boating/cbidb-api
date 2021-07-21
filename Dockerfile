FROM openjdk:11
COPY CBI DB API_0.1.0_all.deb ./
RUN dpkg -i CBI DB API_0.1.0_all.deb
