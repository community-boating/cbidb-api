FROM openjdk:11
RUN mkdir /app
COPY CBI-DB-API_0.1.0_all.deb /app/
RUN dpkg -i /app/CBI-DB-API_0.1.0_all.deb
COPY py-build-routes.py /usr/share/cbi-db-api/
CMD cd /usr/share/cbi-db-api && \
mkdir -p conf/private && \
cp /mnt/conf/server-properties conf/private/server-properties && \
cp /mnt/conf/oracle-credentials conf/private/oracle-credentials && \
touch /usr/share/cbi-db-api/RUNNING_PID && \
rm /usr/share/cbi-db-api/RUNNING_PID && \
exec cbi-db-api \
  -J-Djava.security.egd=file:/dev/./urandom \
  -Dplay.http.secret.key=$PLAY_SECRET \
  -Dconfig.resource=conf/application.conf
