<?php

$fullname = htmlspecialchars($_POST['fullname']);
$email = htmlspecialchars($_POST['email']);
$address = $_POST['address'];
$telephone = htmlspecialchars($_POST['telephone']);
$country = htmlspecialchars($_POST['country']);

if (empty($fullname))
{
	die ('Please enter your full name');
}
if (empty($email))
{
	die ('Please enter your email address');
}
if (!check_email_address($email))
{
	die ('Please enter a valid email address');
}

if (is_multiArrayEmpty($address))
{
	die ('Please enter your address');
}
if (empty($telephone))
{
	die ('Please enter your telephone');
}
if (empty($country))
{
	die ('Please enter your country');
}

$fileName = "cla.xhtml";
$file=fopen($fileName,'rb') or die ('Could not load agreement please retry');
$data = fread( $file, filesize($fileName)) or die ('Could not load agreement please retry');
fclose($file);

$data=str_replace('php_fullname',$fullname,$data);
$data=str_replace('php_email',$email,$data);
$data=str_replace('php_telephone',$telephone,$data);
$data=str_replace('php_country',$country,$data);
$data=str_replace('php_signature','Signed',$data);

$i = 1;
foreach ($address as $addr)
{
	$phpAddress = "php_address".$i;
	$data = str_replace($phpAddress,$addr,$data);
	$i++;
}
/*
 $myFile = "/home/groups/i/ik/ikasaneip/persistent/agreements/$fullname.xhtml";
 $fh = fopen($myFile, 'wb') or die("can't open file");
 fwrite($fh, $data);
 fclose($fh);
 */

$str = chunk_split(base64_encode($data));



$semi_rand = md5(time());
$mime_boundary = "boundary{$semi_rand}";
$headers = "From: donotreply@ikasan.org" . "\n";
$headers .= "MIME-Version: 1.0\n" .
            "Content-Type: multipart/mixed;" .
            " boundary=\"{$mime_boundary}\"\n\n";

$msg = "This is a multi-part message in MIME format.\n\n" .
                    "--{$mime_boundary}\n" .
                   "Content-Type:text/plain; charset=\"iso-8859-1\"\n" .
                   "Content-Transfer-Encoding: 7bit\n\n" .

$msg .= "Thank you for submitting the Contributors agreement, attached is a copy of your signed agreement"."\n\n";

$msg .= "--{$mime_boundary}\n" .
        "Content-Type: application/octet-stream; name=\"{$fileName}\"\n" .
        "Content-Transfer-Encoding: base64\n" .
	"Content-Disposition: attachment; filename=\"{$fileName}\"\n\n" .
$str . "\n\n" .
        "--{$mime_boundary}--";

$to = "contribute@ikasan.org,{$email}";
$subject = "Ikasan Developer Agreement";
$config = "-fdonotreply@ikasan.org";


mail($to, $subject,$msg,$headers,$config);
echo "Agreement submitted, thank you";


function is_multiArrayEmpty($multiarray) {
	if(is_array($multiarray) and !empty($multiarray)){
		$tmp = array_shift($multiarray);
		if(!is_multiArrayEmpty($multiarray) or !is_multiArrayEmpty($tmp)){
			return false;
		}
		return true;
	}
	if(empty($multiarray)){
		return true;
	}
	return false;
}

function check_email_address($email) {
	// First, we check that there's one @ symbol, and that the lengths are right
	if (!ereg("^[^@]{1,64}@[^@]{1,255}$", $email)) {
		// Email invalid because wrong number of characters in one section, or wrong number of @ symbols.
		return false;
	}
	// Split it into sections to make life easier
	$email_array = explode("@", $email);
	$local_array = explode(".", $email_array[0]);
	for ($i = 0; $i < sizeof($local_array); $i++) {
		if (!ereg("^(([A-Za-z0-9!#$%&'*+/=?^_`{|}~-][A-Za-z0-9!#$%&'*+/=?^_`{|}~\.-]{0,63})|(\"[^(\\|\")]{0,62}\"))$", $local_array[$i])) {
			return false;
		}
	}
	if (!ereg("^\[?[0-9\.]+\]?$", $email_array[1])) { // Check if domain is IP. If not, it should be valid domain name
		$domain_array = explode(".", $email_array[1]);
		if (sizeof($domain_array) < 2) {
			return false; // Not enough parts to domain
		}
		for ($i = 0; $i < sizeof($domain_array); $i++) {
			if (!ereg("^(([A-Za-z0-9][A-Za-z0-9-]{0,61}[A-Za-z0-9])|([A-Za-z0-9]+))$", $domain_array[$i])) {
				return false;
			}
		}
	}
	return true;
}
?>
