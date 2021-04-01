<?php
$returnArray=array();

$returnArray=array();

if (empty($_REQUEST["kullaniciAdi"]) || empty($_REQUEST["sifre"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgi veya bilgiler eksik.";
    echo json_encode($returnArray);
    return;
}


$kullaniciAdi = htmlentities($_REQUEST["kullaniciAdi"]);
$sifre = htmlentities($_REQUEST["sifre"]);


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

$user=$access->selectUser($kullaniciAdi);

if (empty($user))
{
    $returnArray["status"] = "400";
    $returnArray["mesaj"] = "Kullanıcı bulunamadı.";
    echo json_encode($returnArray);
    return;

}

$guvenli_sifre=$user["sifre"];
$salt=$user["salt"];

if ($user["emailOnay"]==1 && $guvenli_sifre==sha1($sifre . $salt)){
    // kullanıcı bilgilerini dizide depolayıp daha sonra json olarak döndüreceğiz.
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Kullanıcı girişi başarılı bir şekilde yapıldı.";
    $returnArray["id"] = $user["id"];
    $returnArray["kullaniciAdi"] = $user["kullaniciAdi"];
    $returnArray["mail"] = $user["mail"];
    $returnArray["adSoyad"] = $user["adSoyad"];
    $returnArray["avatar"] = $user["avatar"];

    echo json_encode($returnArray);
    return;
}else if ($user["emailOnay"]==0){
    $returnArray["status"] = "401";
    $returnArray["mesaj"] = "Mail adresinin doğrulanması gerekiyor..";
    echo json_encode($returnArray);
    return;
}
else if ($guvenli_sifre!=sha1($sifre . $salt)){
    $returnArray["status"] = "403";
    $returnArray["mesaj"] = "Şifre yanlış.";
    echo json_encode($returnArray);
    return;
}

$access->disconnect();

echo json_encode($returnArray);




?>
