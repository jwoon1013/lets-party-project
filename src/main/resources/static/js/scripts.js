function getToken() {
    let cName = 'Authorization' + '=';
    let cookieData = document.cookie;
    let cookie = cookieData.indexOf('Authorization');
    let auth = '';
    if (cookie !== -1) {
        cookie += cName.length;
        let end = cookieData.indexOf(';', cookie);
        if (end === -1) end = cookieData.length;
        auth = cookieData.substring(cookie, end);
    }

    // kakao 로그인 사용한 경우 Bearer 추가
    if (auth.indexOf('Bearer') === -1 && auth !== '') {
        auth = 'Bearer ' + auth;
    }

    return auth;
}

