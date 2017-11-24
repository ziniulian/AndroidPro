var user = rfdo.getUser();
if (!user) {
    location.href = "signin.html";
}

function init() {
    userDom.innerHTML += user;
}

function signOut() {
    rfdo.signout();
    location.href = "signin.html";
}
