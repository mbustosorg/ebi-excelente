/*

    Copyright (C) 2015 Mauricio Bustos (m@bustos.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

$(document).ready(function() {

  $('#donation').append('<option value =\"1\">$1</option>');
  for (i = 5; i < 200; i = i + 5) {
    $('#donation').append('<option value =\"' + i + '\">$' + i + '</option>');
  }
  $('#language').append('<option value=\"english\">English</option>');
  $('#language').append('<option value=\"spanish\">Spanish</option>');

  $('#language').change(function() {
     var language = $('#language').val();
     if (language == 'english') $('#excellent').html('is eXcellent because');
     else $('#excellent').html('es eXcelente porque');
  });

  $('#submitButton').click(function() {
    var message = $('#message').val().toLowerCase().trim();
    if (message) {
      var timestamp = (new Date()).toISOString();
      var donation = $('#donation').val();
      var language = $('#language').val();
      $.ajax({
        type: "POST",
        url: "/entry",
        dataType: "json",
        data: '{\"text\": \"' + message + '\", \"timestamp\": \"' + timestamp + '\", \"donation\": ' + Number(donation) + ', \"language\": \"' + language + '\"}',
        error: function(XMLHttpRequest, textStatus, errorThrown) {
          $('#submitAlert').text(XMLHttpRequest.responseText);
          $('#submitAlert').removeClass('hide');
          $('#submitAlert').removeClass('alert-success');
          $('#submitAlert').addClass('alert-danger');
          $('#submitAlert').html('<strong>Enable to submit</strong>');
        },
        success: function(data){
          $('#submitAlert').text(XMLHttpRequest.responseText);
          $('#submitAlert').removeClass('hide');
          $('#submitAlert').addClass('alert-success');
          $('#submitAlert').removeClass('alert-danger');
          $('#submitAlert').html('<strong>Submitted:  </strong>' + message);
        }
      });
    }
  });

});