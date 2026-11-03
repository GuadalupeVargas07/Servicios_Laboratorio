const API_URL = "http://localhost:8085/api/equipos";
const equipoForm = document.getElementById("equipoForm");
const btnGuardar = document.getElementById("btnGuardar");
const btnVerTodo = document.getElementById("btnVerTodo");
const btnVerDisponibles = document.getElementById("btnVerDisponibles");
const mensajeForm = document.getElementById("mensajeForm");
const mensajeTabla = document.getElementById("mensajeTabla");
const tablaEquipos = document.getElementById("tablaEquipos");

document.addEventListener("DOMContentLoaded",()=>{
    cargarDatos(API_URL);
});

btnVerTodo.addEventListener("click",()=>{
    cargarDatos(API_URL);
});

btnVerDisponibles.addEventListener("click",()=>{
    cargarDatos(`${API_URL}/disponibles`);
});

async function cargarDatos(url){
    mensajeTabla.textContent= "Cargando catálogo...";
    mensajeTabla.className= "";
    btnVerTodo.disabled= true;
    btnVerDisponibles.disabled= true;

    const controlador= new AbortController();
    const temporizador= setTimeout(()=> controlador.abort(), 5000);

    try{
        const respuesta= await fetch(url,{
            method: "GET",
            headers:{"Accept": "application/json"},
            signal: controlador.signal
        });

        if (!respuesta.ok){
            mostrarError(mensajeTabla, `Error HTTP ${respuesta.status}`);
            return;
        }

        const equipos= await respuesta.json();
        renderizarTabla(equipos);
        mensajeTabla.textContent= "Catálogo actualizado.";
        mensajeTabla.className= "exito";

    }catch(error){
        if (error.name === "AbortError"){
            mostrarError(mensajeTabla,"El servicio tardó demasiado en responder.");
        }else{
            mostrarError(mensajeTabla, "No se pudo conectar con el servicio en el puerto 8085.");
        }
    }finally{
        clearTimeout(temporizador);
        btnVerTodo.disabled = false;
        btnVerDisponibles.disabled = false;
    }
}

equipoForm.addEventListener("submit", async(evento)=>{
    evento.preventDefault();

    mensajeForm.textContent= "Guardando...";
    mensajeForm.className= "";
    btnGuardar.disabled= true;

    const nuevoEquipo={
        nombre: document.getElementById("nombre").value.trim(),
        marca: document.getElementById("marca").value.trim(),
        modelo: document.getElementById("modelo").value.trim(),
        numeroSerie: document.getElementById("numeroSerie").value.trim(),
        estado: document.getElementById("estado").value,
        disponible: document.getElementById("disponible").value === "true"
    };

    const controlador= new AbortController();
    const temporizador= setTimeout(()=> controlador.abort(), 5000);

    try{
        const respuesta= await fetch(API_URL,{
            method: "POST",
            headers:{
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(nuevoEquipo),
            signal: controlador.signal
        });

        if(!respuesta.ok){
            mostrarError(mensajeForm, `Error HTTP ${respuesta.status} al guardar.`);
            return;
        }

        mensajeForm.textContent = "Equipo registrado exitosamente.";
        mensajeForm.className = "exito";
        equipoForm.reset();

        cargarDatos(API_URL);

    }catch(error){
        if(error.name === "AbortError"){
            mostrarError(mensajeForm, "El servicio tardó demasiado en responder.");
        }else{
            mostrarError(mensajeForm, "Error de red al intentar registrar.");
        }
    }finally{
        clearTimeout(temporizador);
        btnGuardar.disabled = false;
    }
});

function renderizarTabla(equipos){
    tablaEquipos.innerHTML = "";

    if(!Array.isArray(equipos) || equipos.length === 0){
        tablaEquipos.innerHTML = `<tr><td colspan="6" style="text-align:center;">No hay equipos registrados</td></tr>`;
        return;
    }

    equipos.forEach(equipo =>{
        const fila = document.createElement("tr");
        const badgeClass = equipo.disponible ? "badge-true" : "badge-false";
        const textoDisponibilidad = equipo.disponible ? "Sí" : "No";

        fila.innerHTML= `
            <td>${equipo.id}</td>
            <td>${equipo.nombre}</td>
            <td>${equipo.marca} ${equipo.modelo}</td>
            <td>${equipo.numeroSerie}</td>
            <td><strong>${equipo.estado}</strong></td>
            <td><span class="badge ${badgeClass}">${textoDisponibilidad}</span></td>
        `;
        tablaEquipos.appendChild(fila);
    });
}

function mostrarError(elemento, texto){
    elemento.textContent = texto;
    elemento.className = "error";
}