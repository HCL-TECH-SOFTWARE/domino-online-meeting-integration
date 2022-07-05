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
        var ta = document.createElement('textarea');
        ta.value = $(this).next()[0].innerText;
        ta.style.position = 'absolute';
        ta.style.left = '-3000px';
        document.body.appendChild(ta);
        ta.select();
        document.execCommand('copy');
        document.body.removeChild(ta);
        $(this).text("Copied!");
        var self = $(this);
        setTimeout(function () {
            self.text("Copy");
        }, 3000)
  }

$(document).ready(pushdatafromlocation());
$(document).ready(addcopytoken());
