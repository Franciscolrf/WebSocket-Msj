let socket = null;

function conectar() {
  const alias = document.getElementById("txtAlias").value.trim();
  if (alias === "") {
    alert("Debes ingresar un alias.");
    return;
  }

  if (socket && socket.readyState === WebSocket.OPEN) {
    alert("Ya estás conectado.");
    return;
  }

  const url = `ws://localhost:8080/WebSocket/endpoint/${alias}`;
  socket = new WebSocket(url);

  socket.onopen = () => {
    agregarMensaje(" Conectado al servidor como " + alias);
    document.getElementById("txtAlias").disabled = true;
  };

  socket.onmessage = (event) => {
    const mensaje = event.data;

    if (mensaje.startsWith("USUARIOS:")) {
      const lista = mensaje.replace("USUARIOS:", "").split(",");
      actualizarListaUsuarios(lista);
    } else {
      agregarMensaje(mensaje);
    }
  };

  socket.onclose = () => {
    agregarMensaje(" Desconectado del servidor.");
    actualizarListaUsuarios([]);
    document.getElementById("txtAlias").disabled = false;
  };

  socket.onerror = (error) => {
    agregarMensaje(" Error en la conexión: " + error.message);
  };
}


function enviarMensaje() {
    const input = document.getElementById("mensaje");
    const mensaje = input.value.trim();
    if (mensaje === "" || socket == null || socket.readyState !== WebSocket.OPEN) {
        return;
    }
    socket.send(mensaje);
    input.value = "";
}

function agregarMensaje(mensaje) {
    const contenedor = document.getElementById("mensajes");
    const p = document.createElement("p");
    p.textContent = mensaje;
    contenedor.appendChild(p);
    contenedor.scrollTop = contenedor.scrollHeight;
}

function actualizarListaUsuarios(usuarios) {
    const ul = document.getElementById("listaUsuarios");
    ul.innerHTML = ""; // Limpiar lista
    usuarios.forEach(usuario => {
        const li = document.createElement("li");
        li.textContent = usuario;
        ul.appendChild(li);
    });
}
