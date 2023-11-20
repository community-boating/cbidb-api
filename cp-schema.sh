rm ./app/org/sailcbi/APIServer/Api/Endpoints/Dto/* -r
cp ../cbidb-schema/out/api/scala/* ./app/org/sailcbi/APIServer/Api/Endpoints/Dto/ -r

rm ./app/org/sailcbi/APIServer/Entities/EntityDefinitions/* -r
cp ../cbidb-schema/out/entities/* ./app/org/sailcbi/APIServer/Entities/EntityDefinitions/ -r
