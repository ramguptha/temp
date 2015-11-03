(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');  ga('create', 'UA-50107827-2', 'absolute.com');  ga('send', 'pageview');

$(document).ready(function(){

   $('div.search-submit').click(function(){ 
      ga('send','event','Search','Search',document.getElementById('search-field').value); 
   });

   $('input#search-field').keypress(function(event) {
           if (event.keyCode == 13) {
               ga('send','event','Search','Search',document.getElementById('search-field').value);
           }
   });

});