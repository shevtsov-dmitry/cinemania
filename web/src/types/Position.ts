enum Position {
    DIRECTOR = 'DIRECTOR',
    ACTOR = 'ACTOR',
    PRODUCER = 'PRODUCER',
    OPERATOR = 'OPERATOR',
}

const PositionKindLocalized = {
    RU: {
        [Position.DIRECTOR]: 'Режиссёр',
        [Position.ACTOR]: 'Актёр',
        [Position.PRODUCER]: 'Продюсер',
        [Position.OPERATOR]: 'Оператор',
    },
    EN_FROM_RU: {
        ['Режиссёр']: Position.DIRECTOR,
        ['Актёр']: Position.ACTOR,
        ['Продюсер']: Position.PRODUCER,
        ['Оператор']: Position.OPERATOR,
    },
}

export default Position
export { PositionKindLocalized }
