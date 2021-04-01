<?php

$returnArray=array();

if (empty($_REQUEST["uuid"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgiler eksik";
    echo json_encode($returnArray);
    return;
}


$uuid = htmlentities($_REQUEST["uuid"]);



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




$result=$access->deleteTweet($uuid);

if ($result==1) {
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Tweet silindi.";


    if (!empty($_REQUEST["path"])) {

        $path=htmlentities($_REQUEST["path"]);

        // Dosya yolunu düzenliyoruz
        $path = str_replace("http://192.168.1.42/", "C:/xampp/htdocs/", $path);

        // dosya siliniyor ve silme başarılı ise
        if (unlink($path)) {
            $returnArray["mesaj"] = "Tweet ve resmi silindi.";

            // dosya silinemedi ise
        } else {
            $returnArray["status"] = "400";
            $returnArray["mesaj"] = "Tweet resmi silinemedi.";
        }
    }

}	else {
    $returnArray["status"] = "400";
    $returnArray["mesaj"] = "Tweet silinemedi.";
}





// bağlantıyı kapat
$access->disconnect();


// Uygulama kullanıcısına geri bildirim dizisi gönder
echo json_encode($returnArray);

?>