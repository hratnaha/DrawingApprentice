//facebook login
var auth;
    function fbLogin(){
        window.fbAsyncInit = function() {
          FB.init({
            appId      : '561805763922449',
            xfbml      : true,
            version    : 'v2.2'
          });

          // Place following code after FB.init call.

        function onLogin(response) {
          if (response.status == 'connected') {
            FB.api('/me', function(data) {
              console.log("data\n");
              console.log(data);
              sendUserID(data.id, data.name);
              var welcomeBlock = document.getElementById('fb-welcome');
              welcomeBlock.innerHTML = 'Hello, ' + data.first_name + '!';
            });
          }
        }

        FB.getLoginStatus(function(response) {
          // Check login status on load, and if the user is
          // already logged in, go directly to the welcome message.
          if (response.status == 'connected') {
            onLogin(response);
          } else {
            // Otherwise, show Login dialog first.
            FB.login(function(response) {
              onLogin(response);
            }, {scope: 'user_friends, email'});
          }
        });
          };

          (function(d, s, id){
             var js, fjs = d.getElementsByTagName(s)[0];
             if (d.getElementById(id)) {return;}
             js = d.createElement(s); js.id = id;
             js.src = "https://connect.facebook.net/en_US/all.js";
             fjs.parentNode.insertBefore(js, fjs);
           }(document, 'script', 'facebook-jssdk'));
        }

        function authToken(){
          console.log(auth);
        }
