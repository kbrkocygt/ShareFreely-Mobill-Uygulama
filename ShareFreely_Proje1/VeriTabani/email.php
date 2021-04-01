<?php
require 'PHPMailer/src/Exception.php'; //Mail gönderirken bir hata ortaya çıkarsa hata mesajlarını görebilmek için gerekli. Şart değil
require 'PHPMailer/src/PHPMailer.php'; //Mail göndermek için gerekli.
require 'PHPMailer/src/SMTP.php'; //SMTP ile mail göndermek için gerekli.

use PHPMailer\PHPMailer\PHPMailer; //Kullanılacak sınıfın (PHPMailer) yolu belirtiliyor ve projeye dahil ediliyor
class email
{
    function tokenOlustur($uzunluk)
    {
        $karakterler = "qwertyuopasdfghjklzxcvbnmiQWERTYUOPASDFGHJKLZXCVBNMI123456789";//BUNLARDAN Bİ ŞİFRE OLUSACAK
        $karakterSayisi = strlen($karakterler);
        $token = "";
        for ($i = 0; $i < $uzunluk; $i++) {
            $token .= $karakterler[rand(0, $karakterSayisi - 1)];//karakter uzunluğunun bir eksiğine kadar gidicek
        }
        return $token;


    }

    function emailOnaySablonu()
    {

        // dosyayı açıyoruz
        $file = fopen("Sablonlar/emailOnaySablonu.html", "r") or die("Dosya açma işlemi başarısız.");

        // dosya içeriğini $sablon içerisine sakla
        $sablon = fread($file, filesize("Sablonlar/emailOnaySablonu.html"));

        fclose($file);

        return $sablon;

    }

    function sendEmail($detay) {

        $konu = $detay["konu"]; //subject
        $kime = $detay["kime"]; //to
        $fromName = $detay["fromName"]; //gönderici ismi
        $fromEmail = $detay["fromEmail"]; //gönderici mail adresi
        $body = $detay["body"]; //gövde

        //başlık veya üstbilgi
        $headers = "MIME-Version: 1.0" . "\r\n";
        $headers .= "Content-type:text/html;content=UTF-8" . "\r\n";
        $headers .= "From: " . $fromName . " <" . $fromEmail . ">" . "\r\n";

        //mail gönderiliyor
        if(mail($kime, $konu, $body, $headers)){
            return "Mail gönderildi";
        }else{
            return "Mail gönderilemedi";
        }



    }



    function sendEmailPhpMailer($detay)
    {

        $konu = $detay["konu"]; //subject
        $kime = $detay["kime"]; //to
        $body = $detay["body"]; //gövde
        $fromName = $detay["fromName"]; //gönderici ismi


        $mail = new PHPMailer(); //PHPMailer sınıfı kuruluyor

        $mail->Host = 'smtp.gmail.com'; //SMPT mail sunucusu. Ornek: smtp.yandex.com (YANDEX MAIL), smtp.gmail.com (GOOGLE/GMAIL), smtp.live.com (HOTMAIL), mail.ornekmailsunucusu.com (SITENIZE OZEL MAIL SUNUCU)
        $mail->Username = 'kcygt.kbra@gmail.com'; //Tanımlanan web sunucusuna ait mail hesabı kullanıcı adı. Ornek: gonderenmailadresi@yandex.com, mail@domainadresi.com
        $mail->Password = '281017ak'; //Mail hesabı şifre
        $mail->Port = 587; //Mail sunucu mail gönderme portu. Ornek: 587, 465
        $mail->SMTPSecure = 'tls'; //Veri gizliliği yöntemi. Örnek: tls, ssl

        $mail->isSMTP(); //SMPT kullanarak mail gönderilecek
        $mail->SMTPAuth = true; //SMPT kimlik doğrulanmasını etkinleştir

        $mail->isHTML(true); //Mail içeriğinde HTML etiketlerinin algılanmasına izin vermek. False olarak seçilirse ve mail içeriğinde HTML içerikleri varsa etiketler algılanmaksızın normal düz yazı olarak içerikte belirecek

        $mail->CharSet = "UTF-8"; //Mail başlık, konu ve içerikte türkçe karakter desteği mevcut
        $mail->setLanguage('tr', 'language/'); //hata mesajlarını tr dili ile yazdır. 'language' isimli klasörden dil ayarları çekilir. Varsayılan olarak ingilizce seçilidir
        $mail->SMTPDebug = 2; //işlem sürecini göster. Hataları belirlemenizi kolaylaştırır

        $mail->setFrom($mail->Username, $fromName); //Tanımlanan web sunucusuna ait bir gönderen mail adresi ve isim. Username kısmında belirtilen mail adresi ile aynı olmalı. Ornek: gonderenmailadresi@yandex.com, mail@domainadresi.com
//$mail->addReplyTo('gonderenmailadresi2@hotmail.com', 'Muhammed Yaman'); //Mailin gönderildiği kişi maili yanıtlamak isterse buradaki mail adresine mail gönderilmesi gerektiği belirtilir
        $mail->AddAddress($kime);

        $mail->Subject = $konu; //Mail konusu
        $mail->MsgHTML($body);


        if (!$mail->Send()) {
            return "Mailer Hata: " . $mail->ErrorInfo;
        } else {
            return "Mail başarıyla gönderildi!";
        }
    }

    public function sifreSifirlamaMailSablonu()
    {
        // dosyayı açıyoruz
        $file = fopen("Sablonlar/sifreSifirlamaMailSablonu.html", "r") or die("Dosya açma işlemi başarısız.");

        // dosya içeriğini $sablon içerisine sakla
        $sablon = fread($file, filesize("Sablonlar/sifreSifirlamaMailSablonu.html"));

        fclose($file);

        return $sablon;
    }
}
?>