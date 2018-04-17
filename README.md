# Radio 105 (or 90s in Georgia)

This little Clojure application downloads every single mp3 file from radio105.wanex.net.

To run the script with leiningen, you can use
```
lein run
```

To create a standalone `.jar` file, you can run
```
lein uberjar
```

And then run the standalone application from the newly created `target` folder using
```
java -jar radio105-0.0.1-standalone.jar
```
