enum PersonCategory {
    DIRECTOR = 'DIRECTOR',
    ACTOR = 'ACTOR',
    PRODUCER = 'PRODUCER',
    OPERATOR = 'OPERATOR',
}

const PersonCategoryLocalized = {
    RU: {
        [PersonCategory.DIRECTOR]: 'Режиссёр',
        [PersonCategory.ACTOR]: 'Актёр',
        [PersonCategory.PRODUCER]: 'Продюсер',
        [PersonCategory.OPERATOR]: 'Оператор',
    },
}

export default PersonCategory
export { PersonCategoryLocalized }
