<?php

$returnArray=array();

if (empty($_REQUEST["mail"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgiler eksik";
    echo json_encode($returnArray);
    return;
}


$mail = htmlentities($_REQUEST["mail"]);



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




// kullanıcı bilgileri
$user = $access->selectUserMaileGore($mail);

if (empty($user)) {
    $returnArray["status"] = "204";
    $returnArray["mesaj"] = "Mail adresi bulunamadı";
    echo json_encode($returnArray);
    $access->disconnect();
    return;
}

require ("email.php");
$email=new email();
$token=$email->tokenOlustur(20);
$access->saveToken("sifresifirlamatokens",$token,$user["id"]);

//mail gönderilecek
$detay=array();
$detay["konu"]="Şifre sıfırlama işlemi";
$detay["kime"]=$user["mail"];
$detay["fromName"]="Kübra KOÇYİĞİT";
$detay["fromEmail"]="kcygt.kbra@gmail.com";


$sablon=$email->sifreSifirlamaMailSablonu();
$sablon=str_replace("{token}",$token,$sablon);
$detay["body"]=$sablon;

$result=$email->sendEmail($detay);
$returnArray["mailgondermedurumu"] = $result;

$returnArray["status"] = "200";
$returnArray["mesaj"] = "Şifre sıfırlama için mail gönderildi";





// bağlantıyı kapat
$access->disconnect();


// Uygulama kullanıcısına geri bildirim dizisi gönder
echo json_encode($returnArray);

?>