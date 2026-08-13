# Modelo del Negocio

## Entidades

### Usuario
Representa a una persona que utiliza la aplicación para gestionar su colección.

---

### Marca
Representa la marca del vehículo real.
Ejemplo: Ford, Nissan, Toyota.

---

### Modelo
Representa el vehículo base.
Ejemplo: Ford F-150 Raptor.

---

### Variante
Representa una versión específica de un modelo.
Ejemplo: Ford F-150 Raptor Azul Mainline 2024.

---

### Ejemplar
Representa una pieza física que pertenece a un usuario.

## Reglas de negocio

1. Un usuario puede tener varios ejemplares de la misma variante.
2. Una variante pertenece a un único modelo.
3. Un modelo puede tener múltiples variantes.
4. Un ejemplar pertenece a un solo usuario.
5. Un ejemplar puede tener una o varias fotografías.
6. El sistema debe permitir identificar rápidamente si un usuario ya posee una variante específica.