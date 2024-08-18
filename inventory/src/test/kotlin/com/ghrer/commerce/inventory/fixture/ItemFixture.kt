package com.ghrer.commerce.inventory.fixture

import com.ghrer.commerce.inventory.model.Item
import com.ghrer.commerce.inventory.utils.RandomUtil
import java.util.UUID
import kotlin.random.Random

object ItemFixture {

    fun getRandomItem() = Item(
        id = UUID.randomUUID(),
        displayName = RandomUtil.randomItemName(),
        description = RandomUtil.randomDescription(),
        quantity = 10,
        reserved = Random.nextInt(0, 5),
        onShelf = 10,
        price = Random.nextDouble(1.0, 100.0)
    )
}
