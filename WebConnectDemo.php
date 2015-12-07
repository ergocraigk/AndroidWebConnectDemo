<?php

    $json = file_get_contents('php://input');
    
    $json = json_decode($json, true);
    
    

    if($_SERVER['REQUEST_METHOD'] == 'GET'){
        //echo "a string is here";
        if($_GET['dataType'] == 'string'){
            echo "This string was retrieved with a GET request";
        }
    }
    elseif($_SERVER['REQUEST_METHOD'] == 'POST'){
        
        if($_POST['dataType'] == 'string'){
            echo "This string was retrieved with a POST request";
        }
        if( is_array($json)){
            $json['note'] = 'Craig Is Awesome';
            echo json_encode($json);
        }
    }
    else{
        //do nothing
    }

?>