
function validateform(){
	var uname = document.forms["register"]["username"].value;
	var pass1 = document.forms["register"]["password1"].value;
	var pass2 = document.forms["register"]["password2"].value; 	
	if(pass1!=pass2)
	{
		alert("Passwords Missmatch");
		return false;
	}
	else{
		return true;	
	}
	return false;
}

function vlogin(){
	var uname = document.forms["login"]["username"].value;
	if(uname=="sad")
	{
		alert("Hello");
	}
	else{
		window.location.href = "Login.html";
	}
	return false;
}
