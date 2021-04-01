<?php

//echo  "Merhaba... Mail adresiniz  başarılı bir şekilde onaylandı..."
if(empty($_GET["token"]))
{
    echo  "Bilgiler eksik...";
    return;
}
else{
    $token=htmlentities($_GET["token"]);
    //veritabanı bağlantısı
    require ("../access.php");
    $access = new access("localhost", "root", "", "sharefreely");
    $baglanti=$access->connect();


 $id=$access->getUserId("emailtokens",$token);
 if (empty($id["id"]))
 {
     echo "Kullanıcı Bulanamadı...";
     return;
 }
 //Email onaylanma işlemi veritabanına kaydolacak
    $result=$access->emailOnayDurumunuDegistir($id["id"],1);

    if ($result){

        //emailtokens tablosundan token silinecek
        $access->deleteToken("emailtokens",$token);
        echo "Teşekkürler! Mail adresiniz başarılı bir şekilde onaylandı.";
    }

    $access->disconnect();//baglantıyı kapat



}
?>
