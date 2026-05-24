(function () {
  if (typeof Android === 'undefined') return;

  var hideMode = Android.getHideMode();
  var ignoredUsers = JSON.parse(Android.getIgnoredUsersJson());
  var lastUpdated = Android.getLastUpdatedMs();

  function timeAgo(ms) {
    if (!ms) return 'nunca';
    var diff = Date.now() - ms;
    var minutes = Math.floor(diff / 60000);
    var hours = Math.floor(diff / 3600000);
    if (minutes < 1) return 'hace menos de 1 minuto';
    if (minutes === 1) return 'hace 1 minuto';
    if (hours < 1) return 'hace ' + minutes + ' minutos';
    if (hours === 1) return 'hace 1 hora';
    return 'hace ' + hours + ' horas';
  }

  function escAttr(s) {
    return s.replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/</g,'&lt;');
  }

  function escJs(s) {
    return s.replace(/\\/g,'\\\\').replace(/'/g,"\\'");
  }

  var userRows = ignoredUsers.length === 0
    ? '<div style="color:#666;font-size:12px;padding:4px 0;">No hay usuarios ignorados</div>'
    : ignoredUsers.map(function(u) {
        return '<div style="display:flex;justify-content:space-between;align-items:center;' +
               'padding:6px 0;border-bottom:1px solid #2a2a2a;" data-user="' + escAttr(u) + '">' +
               '<span style="color:#ccc;">@' + escAttr(u) + '</span>' +
               '<button onclick="fcRemoveUser(\'' + escJs(u) + '\')" style="' +
               'background:none;border:none;color:#ff5555;cursor:pointer;font-size:13px;padding:2px 8px;">✕</button>' +
               '</div>';
      }).join('');

  var isChecked = hideMode === 'complete';
  var sliderBg = isChecked ? '#00e5cc' : '#555';
  var thumbLeft = isChecked ? '21px' : '3px';

  var panel = document.createElement('div');
  panel.id = 'fc-settings-panel';
  panel.innerHTML =
    '<div style="background:#1a1a1a;border:1px solid #333;border-radius:8px;' +
    'padding:16px;margin:12px 8px;color:#e0e0e0;font-family:sans-serif;font-size:14px;">' +

    '<div style="font-weight:bold;font-size:15px;margin-bottom:14px;color:#00e5cc;">⚙ FC Configuración</div>' +

    '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px;">' +
    '<span>Ocultar hilos completamente</span>' +
    '<label style="position:relative;width:40px;height:22px;flex-shrink:0;cursor:pointer;">' +
    '<input type="checkbox" id="fc-hide-toggle" ' + (isChecked ? 'checked' : '') +
    ' style="opacity:0;width:0;height:0;position:absolute;">' +
    '<span id="fc-toggle-track" style="position:absolute;inset:0;background:' + sliderBg +
    ';border-radius:22px;transition:background 0.2s;">' +
    '<span id="fc-toggle-thumb" style="position:absolute;width:16px;height:16px;left:' + thumbLeft +
    ';bottom:3px;background:#fff;border-radius:50%;transition:left 0.2s;"></span>' +
    '</span></label>' +
    '</div>' +

    '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">' +
    '<span style="color:#888;font-size:12px;">Actualizado: ' + timeAgo(lastUpdated) + ' · ' + ignoredUsers.length + ' ignorados</span>' +
    '<button id="fc-refresh-btn" onclick="fcRefresh()" style="background:none;border:1px solid #00e5cc;color:#00e5cc;border-radius:4px;padding:2px 8px;font-size:11px;cursor:pointer;">↻ Actualizar</button>' +
    '</div>' +

    '<div id="fc-ignored-list" style="max-height:220px;overflow-y:auto;">' +
    userRows +
    '</div>' +
    '</div>';

  window.fcRemoveUser = function(username) {
    Android.removeIgnoredUser(username);
    var el = panel.querySelector('[data-user="' + escAttr(username) + '"]');
    if (el) el.remove();
    var remaining = panel.querySelectorAll('[data-user]').length;
    if (remaining === 0) {
      document.getElementById('fc-ignored-list').innerHTML =
        '<div style="color:#666;font-size:12px;padding:4px 0;">No hay usuarios ignorados</div>';
    }
    var meta = panel.querySelector('div[style*="Actualizado"]');
    if (meta) meta.textContent = 'Actualizado: ' + timeAgo(Android.getLastUpdatedMs()) +
      ' · ' + remaining + ' ignorados';
  };

  window.fcRefresh = function() {
    var btn = document.getElementById('fc-refresh-btn');
    if (btn) { btn.textContent = '↻ ...'; btn.disabled = true; }
    Android.triggerRefresh();
    setTimeout(function() {
      var users = JSON.parse(Android.getIgnoredUsersJson());
      var list = document.getElementById('fc-ignored-list');
      if (list) {
        list.innerHTML = users.length === 0
          ? '<div style="color:#666;font-size:12px;padding:4px 0;">No hay usuarios ignorados</div>'
          : users.map(function(u) {
              return '<div style="display:flex;justify-content:space-between;align-items:center;' +
                     'padding:6px 0;border-bottom:1px solid #2a2a2a;" data-user="' + escAttr(u) + '">' +
                     '<span style="color:#ccc;">@' + escAttr(u) + '</span>' +
                     '<button onclick="fcRemoveUser(\'' + escJs(u) + '\')" style="' +
                     'background:none;border:none;color:#ff5555;cursor:pointer;font-size:13px;padding:2px 8px;">✕</button>' +
                     '</div>';
            }).join('');
      }
      var meta = panel.querySelector('span[style*="Actualizado"]');
      if (meta) meta.textContent = 'Actualizado: ' + timeAgo(Android.getLastUpdatedMs()) + ' · ' + users.length + ' ignorados';
      if (btn) { btn.textContent = '↻ Actualizar'; btn.disabled = false; }
    }, 4000);
  };

  panel.querySelector('#fc-hide-toggle').addEventListener('change', function() {
    var mode = this.checked ? 'complete' : 'message';
    Android.setHideMode(mode);
    panel.querySelector('#fc-toggle-track').style.background = this.checked ? '#00e5cc' : '#555';
    panel.querySelector('#fc-toggle-thumb').style.left = this.checked ? '21px' : '3px';
  });

  var anchor = document.querySelector('.member_username')
    || document.querySelector('.memberinfo')
    || document.querySelector('.blockrow')
    || document.querySelector('table.tborder')
    || document.body.firstElementChild;

  if (anchor && anchor !== document.body.firstElementChild) {
    anchor.insertAdjacentElement('afterend', panel);
  } else {
    document.body.insertAdjacentElement('afterbegin', panel);
  }
})();
