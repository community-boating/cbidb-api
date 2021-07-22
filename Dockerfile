FROM openjdk:11
RUN mkdir /app
COPY CBI-DB-API_0.1.0_all.deb /app/
RUN dpkg -i /app/CBI-DB-API_0.1.0_all.deb
COPY py-build-routes.py /usr/share/cbi-db-api/
COPY start.sh.template /usr/share/cbi-db-api/start.sh
CMD ["cd /usr/share/cbi-db-api", "./py-build-routes.py", "./start.sh"]
