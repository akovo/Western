$("#pos").click(function(){
    CheckPosition();
});
$( document ).ready(
function() {
      var token = localStorage.getItem("token");
       var request = new Request('/api/portfolio/value',{
             method: 'GET',
             headers: new Headers({
              'Content-Type': '   application/json',
                 'X-Requested-With': 'XMLHttpRequest',
                 'Cache-Control' : 'no-cache',
                 'Access-Control-Allow-Origin':'*',
              'Authorization':'Bearer' + ' ' + token
             })
         });
            fetch(request).then( function(resp){
                resp.json().then(function(data) {
                  console.log(data);
                  value = data.cash + data.equity;
                  $("#portfolioVal").html('$'+value);
                  $("#percentReturn").html(100*(value-100000)/100000 + '%');
                  $("#grossIncome").html('$'+(value-100000));
                  console.log(data);
                });
            }).catch(err =>{
            console.log(err);
            });
});


    
   
