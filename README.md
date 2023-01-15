# testapp

`lein ring server`
start development mode with nREPL in Ring

`lein test`
run tests

`npx tailwindcss -i ./src/frontend/tailwind.css -o ./resources/public/output.css`
compile css output file

`npx shadow-cljs watch frontend`
start development frontend

`npx shadow-cljs release frontend`
compile frontend build

`lein ring uberjar`
create uberjar

`java -jar ./target/testapp.jar`
start app by executable jar file with db uri env variable


## Unlicenced

Find the full unlicense in the UNLICENSE file
For more information, please refer to <http://unlicense.org>

