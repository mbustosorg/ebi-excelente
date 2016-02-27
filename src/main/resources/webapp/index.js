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
  $('#submitButton').click(function() {
    var message = $('#message').val().toLowerCase().trim();
    if (message) {
      var timestamp = (new Date()).toISOString();
      $.ajax({
        type: "POST",
        url: "/entry",
        dataType: "json",
        data: '{\"text\": \"' + message + '\", \"timestamp\": \"' + timestamp + '\"}',
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
          $('#submitAlert').html('<strong>Submitted:</strong>' + message);
        }
      });
    }
  });

});