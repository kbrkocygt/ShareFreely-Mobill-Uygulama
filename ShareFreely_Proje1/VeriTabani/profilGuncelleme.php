<?php
if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'access.php';
    show();
}

function show()
{
    global $connect;

    $query = "SELECT * FROM  kisiler";

 $result = mysqli_query($connect, $query);
 $number_of_rows = mysqli_num_rows($result);

 $temp_array  = array();

 if($number_of_rows > 0) {
     while ($row = mysqli_fetch_assoc($result)) {
         $temp_array[] = $row;
     }
 }

 header('Content-Type: application/json');
 echo json_encode(array("showarray"=>$temp_array));
 mysqli_close($connect);

}
?>