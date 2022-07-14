/*!
  * copyToken.js
  * Copyright 2022 HCL (https://hcltechsw.com)
  * Licensed under Apache License, Version 2.0, January 2004 (http://www.apache.org/licenses/)
  */

   function pushdatafromlocation() {
    var div = document.getElementById("divlocation");
    var content = div.innerText;
    window.history.pushState('data', '', content);
  }


  function addcopytoken() {
    var btn = document.getElementById('btncopy');

    btn.onclick = function (e) {
      var sp = document.getElementById('refreshToken');
      var token = sp.innerText;

      var ta = document.createElement('textarea');
      // ta.value = $(this).next()[0].innerText;
      ta.value = token;
      ta.style.position = 'absolute';
      ta.style.left = '-3000px';
      document.body.appendChild(ta);
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);

      var self = $(this);
      self.text("Copied!");
      setTimeout(function () {
          self.text("Copy");
      }, 3000)
    }

    btn.classList.remove('visibility-hidden');
    btn.classList.remove('display-none');

  }

if(window.addEventListener){
  window.addEventListener('load', pushdatafromlocation())
}else{
  window.attachEvent('onload', pushdatafromlocation())
}

if(window.addEventListener){
  window.addEventListener('load', addcopytoken())
}else{
  window.attachEvent('onload', addcopytoken())
}
