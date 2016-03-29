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

  $('#donation').append('<option value =\"20\">$20</option>');
  $('#donation').append('<option value =\"50\">$50</option>');
  $('#donation').append('<option value =\"100\">$100</option>');
  $('#language').append('<option value=\"english\">English</option>');
  $('#language').append('<option value=\"spanish\">Spanish</option>');

  $('#language').change(function() {
     var language = $('#language').val();
     if (language == 'english') $('#excellent').html('is eXcellent because');
     else $('#excellent').html('es eXcelente porque');
  });

  function listEntries() {
        $.ajax({
            url: '/entries',
            cache: false
        }).done (function (entries) {
            $('tbody#entry_table_body').empty();
            $.each(entries, function(key, currentEntry) {
                $('#entry_table_body').append(
                    '<tr id="entry' + currentEntry.timestamp + '">' +
                    '<td>' + currentEntry.subject + '</td>' +
                    '<td>' + currentEntry.adjective + '</td>' +
                    '<td>' + currentEntry.language + '</td>' +
                    '<td style="text-align: right">$' + currentEntry.donation + '</td>' +
                    '<td style="text-align: right">' + formatTimestamp(new Date(currentEntry.timestamp)) + '</td>' +
                    '</tr>'
                );
            });
        });
  };

  function formatTimestamp(timestamp) {
      var hours = timestamp.getHours();
      var minutes = timestamp.getMinutes();
      var ampm = " AM";
      if (hours > 12) {
         hours -= 12;
         if (hours == 0) hours = 12;
         ampm = " PM";
      }
      if (minutes < 10) minutes = "0" + minutes;
      return hours + ":" + minutes + ampm;
  };

  $('#submitButton').click(function() {
    var subject = $('#subject').val().toLowerCase().trim();
    var adjective = $('#adjective').val().toLowerCase().trim();
    if (subject && adjective) {
      var timestamp = (new Date()).toISOString();
      var donation = $('#donation').val();
      var language = $('#language').val();
      $.ajax({
        type: "POST",
        url: "/entry",
        dataType: "json",
        data: '{\"subject\": \"' + subject + '\", \"adjective\": \"' + adjective + '\", \"timestamp\": \"' + timestamp + '\", \"donation\": ' + Number(donation) + ', \"language\": \"' + language + '\"}',
        error: function(XMLHttpRequest, textStatus, errorThrown) {
          $('#submitAlert').text(XMLHttpRequest.responseText);
          $('#submitAlert').removeClass('hide');
          $('#submitAlert').removeClass('alert-success');
          $('#submitAlert').addClass('alert-danger');
          $('#submitAlert').html('<strong>Unable to submit, please retry.</strong>');
        },
        success: function(data){
          $('#submitAlert').text(XMLHttpRequest.responseText);
          $('#submitAlert').removeClass('hide');
          $('#submitAlert').addClass('alert-success');
          $('#submitAlert').removeClass('alert-danger');
          $('#submitAlert').html('<strong>Submitted: </strong> ' + subject + ' : ' + adjective);
          listEntries();
        }
      });
    }
  });

  listEntries();
});