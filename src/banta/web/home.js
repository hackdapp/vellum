
var server = mockServer;
var dom = {};
var state = {};

function locationDev() {
    return window.location.hostname.startsWith('localhost') || window.location.hostname.startsWith("192.168");
}

function locationLive() {
    return window.location.hostname.indexOf('appcentral.info') > 0;
}

$(document).ready(function() {
    utilInit();
    documentLoad();
});

function documentLoad() {
    console.log('documentLoad');
    state.loadedComponentsCount = 0;
    load('contacts');
    load('contactEdit');
    load('chats');
    load('chat');
    load('chatContacts');
}

function load(name) {
    $('#' + name + '-container').load(name + '.html', function() {
        documentLoaded(name);
    });
}

function documentLoaded(component) {
    state.loadedComponentsCount++;
    console.log('documentLoaded', state.loadedComponentsCount, component);
    if (state.loadedComponentsCount === 5) {
        documentReady();
    }
}

function documentReady() {
    console.log('documentReady');
    $('.home-clickable').click(homeClick);
    $('.reload-clickable').click(reloadClick);
    $('.about-clickable').click(aboutClick);
    $('.contact-clickable').click(contactClick);
    $('.contacts-clickable').click(contactsClick);
    $('.logout-clickable').click(logoutClick);
    $('.chat-clickable').click(chatClick);
    $('.chats-clickable').click(chatsClick);
    chatLoaded();
    chatsLoaded();
    contactsLoaded();
    contactEditLoaded();
    window.addEventListener("popstate", function(event) {
        windowState(event);
    });
    server.documentReady();
}

function windowState(event) {
    console.log("windowState", window.location.pathname);
    event.preventDefault();
    windowLocation(window.location.pathname);
}

function windowLocation(pathname) {
    console.log("windowLocation", pathname, window.location.pathname);
    if (pathname === '/#home') {
        homeClick();
    } else if (pathname === '/#contactUs') {
        contactClick();
    } else if (pathname === '/#aboutUs') {
        aboutClick();
    } else if (pathname === '/#contacts') {
        contactsClick();
    } else if (pathname.startsWith('/#contactEdit/')) {
        contactsClick();
    } else if (pathname === '/#contactAdd') {
        contactAddClick();
    } else if (pathname === '/#chats') {
        chatsClick();
    } else if (pathname.startsWith('/#chat/')) {
        chatsClick();
    } else {
        homeClick();
    }
}

function removeCookies() {
    $.removeCookie('googleAuth');
}

function showLanding() {
    showLoggedOut();
}

function showLoggedOut() {
    state.auth = null;
    $('.page-container').hide();
    $('.loggedin-viewable').hide();
    $('.logout-clickable').hide();
    $('.loggedout-viewable').show();
    $("#landing-container").show();
}

function showLoggedInRes() {
    if (state.auth === null) {
        console.warn('no server auth');
        state.auth = 'unknown';
    }
    notify('Welcome, ' + state.login.email);
    $('#loggedin-message').text("Welcome, " + state.login.name);
    if (false) {
        $('#loggedin-username-clickable').text(state.login.email);
        $('#loggedin-username-clickable').show();
    }
    showLoggedIn();
}

function showLoggedIn() {
    $('.login-clickable').hide();
    $('.page-container').hide();
    $('.loggedout-viewable').hide();
    $('.loggedin-viewable').show();
    $('.logout-clickable').show();
    $('#loggedin-username').show();
    $('#loggedin-info').show();
    $('#welcome-container').show();
}

function loginRes(res) {
    console.log("loginRes", window.location.pathname);
    if (res.email !== null) {
        state.login = res;
        state.contacts = res.contacts;
        state.chats = res.chats;
        showLoggedInRes();
        if (state.auth === 'google') {
            $.cookie("googleAuth", res.email);
        }
        var path = $.cookie('path');
        console.log('loginRes path', path);
        if (!isEmpty(path)) {
            windowLocation(path);
        } else {
            windowLocation(window.location.pathname);
        }
    }
}

function loginError() {
    console.log("googleLoginError");
    showLoggedOut();
}

function logoutClick(event) {
    if (state.auth === 'persona') {
        state.auth = null;
        personaLogoutClick();
    } else {
        state.auth = null;
        logoutReq();
    }
}

function logoutReq() {
    showLoggedOut();
    server.ajax({
        type: 'POST',
        url: '/logout',
        data: null,
        success: logoutRes,
        error: logoutError
    });
}

function logoutRes(res) {
    console.log("logoutRes");
    console.log(res);
}

function logoutError() {
    console.log("logoutError");
}

function showPage(title, page, path, id) {
    $('#title').text(title);    
    $('.page-container').hide();
    $('#' + page + '-container').show();
    setPath(path, id);
}

function setPath(path, id) {
    path = '/#' + path;
    if (id) {
        path += '/' + id.replace(/\s+/g, '');
    }
    window.history.pushState(null, null, path);
    $.cookie('path', path);
    console.log('setPath', $.cookie('path'));
}

function aboutClick() {
    setPath('aboutUs');
    $('#title').text('About');
    $('.nav-item').removeClass("active");
    $('.page-container').hide();
    $("#about-container").show();
}

function homeClick() {
    state.contact = null;
    setPath('home');
    $('.btn').removeClass('btn-primary');
    if (isEmpty(state.contacts)) {
        $('.contactAdd-clickable').addClass('btn-primary');        
    } else if (isEmpty(state.chats)) {
        $('.chatNew-clickable').addClass('btn-primary');
    } else if (state.chats) {
        $('.chats-clickable').addClass('btn-primary');
    } else {
        $('.contacts-clickable').addClass('btn-primary');        
    }
    $('#title').text('Banta');
    $('.nav-item').removeClass("active");
    $('.page-container').hide();
    if (state.auth === null) {
        $("#landing-container").show();
    } else {
        $("#home-container").show();
    }
}

function contactClick() {
    setPath('cotactUs');
    $('#title').text('Contact us');
    $('.nav-item').removeClass("active");
    $('.page-container').hide();
    $("#contact-container").show();
}

function reloadClick() {
    console.log('reload');
    console.log(window.location.origin);
    if (true) {
        removeCookies();
    }
    window.location.reload();
}

function compareName(a, b) {
    if (a.name === b.name) {
        return 0;
    } else if (a.name.toLowerCase() > b.name.toLowerCase()) {
        return 1;
    }
    return -1;
}

function matchName(object, name) {
    return object.name === name;
}
