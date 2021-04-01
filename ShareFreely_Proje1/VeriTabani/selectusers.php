
<?php

$returnArray=array();

if (empty($_REQUEST["id"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgi veya bilgiler eksik.";
    echo json_encode($returnArray);
    return;
}

$id = htmlentities($_REQUEST["id"]);

//veritabanı bağlantısı
require ("access.php");
$access = new access("localhost", "root", "", "sharefreely");
$baglanti=$access->connect();


if (!$baglanti)
{
    $returnArray["status"] = "404";
    $returnArray["mesaj"] = "Bağlantı başarısız.";
    echo json_encode($returnArray);
    return;
}





// word değeri boş ise null olarak ayarlar
$word = null;

// word değeri boş deği ise $word değişkenine atar
if (!empty($_REQUEST["word"])) {
    $word = htmlentities($_REQUEST["word"]);
}




$kullanicilar = $access->selectUsers($word, $id);


if (!empty($kullanicilar)) {
    $returnArray["kullanicilar"] = $kullanicilar;
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Başarılı";

} else {
    $returnArray["status"] = "204";
    $returnArray["mesaj"] = 'Kayıt bulunamadı';
}




$access->disconnect();


echo json_encode($returnArray);



?>