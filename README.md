# testapp
## dev:
`lein ring server`
- start Ring server with nREPL

`lein test`
- run backend tests
- frontend tests starts from repl by run-tests

`npx shadow-cljs watch frontend`

- start development mode for frontend

## build:
1. `npx tailwindcss -i ./src/frontend/tailwind.css -o ./resources/public/output.css`
- compile css output file

2. `npx shadow-cljs release frontend`
- compile js output build

3. `lein ring uberjar`
- create uberjar

4. `java -jar ./target/testapp.jar`
- start app by executable jar file


## Unlicenced

Find the full unlicense in the UNLICENSE file

For more information, please refer to <http://unlicense.org>

