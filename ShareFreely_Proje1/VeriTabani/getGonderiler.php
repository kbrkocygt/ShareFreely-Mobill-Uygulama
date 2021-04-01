<?php

$returnArray=array();

if (empty($_REQUEST["id"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgiler eksik";
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




$returnArray["tweetler"]=$access->getKisiTweetleri($id);


$returnArray["status"] = "200";
$returnArray["mesaj"] = "Başarılı";



// bağlantıyı kapat
$access->disconnect();


// Uygulama kullanıcısına geri bildirim dizisi gönder
echo json_encode($returnArray);

?>