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

`DB_URI=datomic:mem://prod java -jar ./target/testapp.jar`
start app by executable jar file with db uri env variable


## License

Copyright © 2022 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
