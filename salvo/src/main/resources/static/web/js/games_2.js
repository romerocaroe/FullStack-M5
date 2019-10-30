$(function() {
      $("#logout-btn").hide();
  loadData();
  $("#login-btn").click(function(){
    login();
  });
  $("#logout-btn").click(function(){
    logout();
  });
});

function updateViewGames(data) {
    user  =   data.player;
        if(user  != "Guest"){
            $("#user-info").text("Welcome " +    user.email);
            showLogin(false);
        }
  var htmlList = data.games.map(function (game) {
      return  '<li class="list-group-item">' + new Date(game.created).toLocaleString() + ' ' + game.gamePlayers.map(function(element) { return element.player.email}).join(', ')  +'</li>';
  }).join('');
  document.getElementById("game-list").innerHTML = htmlList;
}

function updateViewLBoard(data) {
  var htmlList = data.map(function (score) {
      return  '<tr><td>' + score.email + '</td>'
              + '<td>' + score.score.total + '</td>'
              + '<td>' + score.score.won + '</td>'
              + '<td>' + score.score.lost + '</td>'
              + '<td>' + score.score.tied + '</td></tr>';
  }).join('');
  document.getElementById("leader-list").innerHTML = htmlList;
}

function loadData() {

  $.get("/api/games")
    .done(function(data) {
      updateViewGames(data);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
  
  $.get("/api/leaderBoard")
    .done(function(data) {
      updateViewLBoard(data);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
}

function login(){
  $.post("/api/login",
    { name: $("#username").val(),
        pwd: $("#password").val()})
    .done(function() {
        console.log("successful login!!")
        loadData(),
      showLogin(false);
    });
}

function logout(){
  $.post("/api/logout")
    .done(function() {
      console.log("successful logout!!")
      showLogin(true);
    });
}

function showLogin(show){
  if(show){
    $("#login-panel").show();
    $("#user-panel").hide();
  } else {
        $("#logout-btn").show();
    $("#login-panel").hide();
    $("#user-panel").show();
  }
}