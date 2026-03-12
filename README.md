Amateras Modelerで更新サイトを自動ビルドできるか確認するためのリポジトリです。

ビルド後、gh-pagesブランチに成果物がプッシュされ updatesite/master の内容が git pagesを通して以下のURLで更新サイトが公開されてます。

masterからのビルドされた更新サイト https://tatoo2018.github.io/amateras-modeler/updatesite/master/

作業内容

・オリジナルのEclipseプロジェクトがJavaSE-1.6の指定だったが、手元に環境がないので1.8にあげた ・各プロジェクトをEclipseからmavenプロジェクトへ変換 ・各pom.xmlを設定 ・一部ソースで手元でコンパイルが通らなかったので、変更。 　net.java.amateras.db/src/net/java/amateras/db/visual/action/CopyAction.java net.java.amateras.db/src/net/java/amateras/db/visual/action/Logical2PhysicalAction.java net.java.amateras.db/src/net/java/amateras/db/visual/action/LowercaseAction.java net.java.amateras.db/src/net/java/amateras/db/visual/action/Physical2LogicalAction.java net.java.amateras.db/src/net/java/amateras/db/visual/action/UppercaseAction.java

before List selection = getSelectedObjects();

after List selection = getSelectedObjects();

・更新サイト用にカテゴリーを明示的に設定しないとうまくインストールできなかなったので TOOLというカテゴリーをつくってビルド cateory.xmlも追加
