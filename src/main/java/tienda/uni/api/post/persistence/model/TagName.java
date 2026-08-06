package tienda.uni.api.post.persistence.model;

public enum TagName {

/*
  los tags estan divididos en 3 tipos
    - categoria: la publicacion es de comida, electronicos, etc.
    - metodo de pago: el vendedor acepta, efectivo, targeta y/o transferencia
    - el tipo de venta: solo por pedidos, entrega, recoleccion, acepta apartados
 */

    // tags de categoria
    FOOD, DRINKS, ELECTRONICS, CLOTHING, FURNITURE, BOOKS, TOYS, BEAUTY, SPORTS, AUTOMOTIVE, PETS, MUSIC, ART, JEWELRY, GARDENING, HEALTH, TRAVEL, HOBBIES,

    // metodos de pago
    CASH, BANK_TRANSFER, PAYPAL, CRYPTOCURRENCY, DEBIT_CARD,

    // tipo de venta (ninguna)
    NO_SALES
}
