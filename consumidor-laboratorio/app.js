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

//Servicio 4: Mantenimiento (Puerto 8088)
const API_MANTENIMIENTO = "http://localhost:8088/api/mantenimiento";
const mantenimientoForm = document.getElementById("mantenimientoForm");
const btnReportarFalla = document.getElementById("btnRegistrarMantenimiento");
const btnVerReportes = document.getElementById("btnVerMantenimiento");
const mensajeMantForm = document.getElementById("mensajeMantenimiento");
const mensajeTablaMant = document.getElementById("mensajeTablaMant");
const tablaReportes = document.getElementById("tablaReportes");
document.addEventListener("DOMContentLoaded", () => {
    cargarReportes();
});
btnVerReportes.addEventListener("click", () => {
    cargarReportes();
});
async function cargarReportes() {
    mensajeTablaMant.textContent = "Cargando reportes...";
    mensajeTablaMant.className = "";
    try {
        const respuesta = await fetch(`${API_MANTENIMIENTO}/reportes`);
        if (!respuesta.ok) {
            mostrarError(mensajeTablaMant, `Error HTTP ${respuesta.status}`);
            return;
        }
        const reportes = await respuesta.json();
        renderizarReportes(reportes);
        mensajeTablaMant.textContent = "Reportes actualizados.";
        mensajeTablaMant.className = "exito";
    } catch (error) {
        mostrarError(mensajeTablaMant, "No se pudo conectar con Mantenimiento en el puerto 8088.");
    }
}
mantenimientoForm.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    mensajeMantForm.textContent = "Guardando reporte...";
    mensajeMantForm.className = "";
    btnReportarFalla.disabled = true;
    const nuevoReporte = {
        equipoId: Number(document.getElementById("mantEquipoId").value),
        descripcionFalla: document.getElementById("mantDescripcion").value.trim()
    };
    try {
        const respuesta = await fetch(`${API_MANTENIMIENTO}/reportes`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(nuevoReporte)
        });
        const datos = await respuesta.json().catch(() => null);
        if (!respuesta.ok) {
            mostrarError(mensajeMantForm, datos?.mensaje || `Error HTTP ${respuesta.status}`);
            return;
        }
        mensajeMantForm.textContent = "Falla reportada y equipo bloqueado.";
        mensajeMantForm.className = "exito";
        mantenimientoForm.reset();
        cargarReportes();
        cargarDatos(API_URL); // Actualiza la tabla de arriba del Servicio 1
    } catch (error) {
        mostrarError(mensajeMantForm, "Error de red al reportar falla.");
    } finally {
        btnReportarFalla.disabled = false;
    }
});
async function liberarEquipo(reporteId) {
    try {
        const respuesta = await fetch(`${API_MANTENIMIENTO}/reportes/${reporteId}/liberacion`, {
            method: "PUT",
            headers: { "Accept": "application/json" }
        });
        if (!respuesta.ok) {
            mostrarError(mensajeTablaMant, `Error HTTP ${respuesta.status} al liberar.`);
            return;
        }
        mensajeTablaMant.textContent = "Equipo liberado exitosamente.";
        mensajeTablaMant.className = "exito";
        cargarReportes();
        cargarDatos(API_URL); // Actualiza la tabla de arriba del Servicio 1
    } catch (error) {
        mostrarError(mensajeTablaMant, "Error de red al liberar el equipo.");
    }
}
function renderizarReportes(reportes) {
    tablaReportes.innerHTML = "";
    if (!Array.isArray(reportes) || reportes.length === 0) {
        tablaReportes.innerHTML = `<tr><td colspan="5" style="text-align:center;">No hay reportes registrados</td></tr>`;
        return;
    }
    reportes.forEach(reporte => {
        const fila = document.createElement("tr");
        const liberado = reporte.estado === "LIBERADO";
        const badgeClass = liberado ? "badge-true" : "badge-false";
        const botonAccion = liberado
            ? "Liberado"
            : `<button type="button" class="btn-verde" onclick="liberarEquipo(${reporte.id})">Liberar</button>`;
        fila.innerHTML = `
            <td>${reporte.id}</td>
            <td>${reporte.equipoId}</td>
            <td>${reporte.descripcionFalla}</td>
            <td><span class="badge ${badgeClass}">${reporte.estado}</span></td>
            <td>${botonAccion}</td>
        `;
        tablaReportes.appendChild(fila);
    });
}