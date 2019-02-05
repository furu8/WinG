## 内容物
- wing-la-dummylib-1.0.jar
  - 参照ライブラリ
- wing-la-dummylib-1.0-javadoc.jar
  - Eclipse上でJavadocを閲覧したい場合、上記の参照ライブラリと関連付ける
- javadoc
  - ブラウザ上で見れるJavadoc。index.htmlを参照。

## Eclipse使用時の参照ライブラリ設定方法
1. メニューの[Project] > [Properties] を選択し、プロジェクトの設定ウィンドウを表示
2. ウィンドウのリストにある [Java Build Path]を選択
3. Java Build Pathのタブの [Libraries]を選択
4. 右側にある[Add External Jars]ボタンをクリックし、内容物の"wing-la-dummylib-1.0.jar"を指定
5. "wing-la-dummylib-1.0.jar" がリストに追加されれば完了

## 参照ライブラリにJavadocを関連付ける方法(上記の参照ライブラリ設定方法を既に完了しているものとする)
関連付けることで、Eclipseのエディタ部分においてWinG側から提供されるクラスやメソッドをカーソルで選択した状態で、
"F2"キーを押すことでそのクラスやメソッドの詳細が表示される。

1. リストに追加された"wing-la-dummylib-1.0.jar"を選択
2. プルダウンで表示されたメニューの [Javadoc location: (None)] をダブルクリック
3. 表示されたウィンドウのラジオボタン [Javadoc in archive] を選択(アクティブ)
4. Active pathの項目の[Browse...]ボタンをクリックし、内容物の"wing-la-dummylib-1.0-javadoc.jar"を指定
5. ウィンドウの[OK]ボタンをクリック
6. 2で選択した [Javadoc location:(None)] が[Javadoc location: {"wing-la-dummylib-1.0-javadoc.jar"を示すパス}]をになっていれば完了
