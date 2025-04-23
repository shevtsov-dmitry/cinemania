import { View, Text, Image } from "react-native";
import { ReactElement } from "react";

const Footer = (): ReactElement => {
  return (
    <View className="bg-neutral-900 w-full py-5">
      <View className="flex-row justify-around items-center flex-wrap">
        {/* Левая часть — логотип и описание */}
        <View className="max-w-[50%] pr-4">
          {/* Логотип компании */}
          <Image
            source={require("@/images/icons/company_logo.png")}
            className="w-2 mb-2"
          />
          <Text className="text-white text-s leading-4">
            Онлайн-платформа для стриминга видео. Мы предоставляем доступ к
            фильмам, сериалам и другому видеоконтенту в любое время на любых
            устройствах.
          </Text>
        </View>

        {/* Правая часть — контакты */}
        <View className="max-w-[45%] items-end">
          <View className={" flex flex-row gap-3"}>
            <Text className="text-white text-xs underline">Адрес офиса:</Text>
            <Text
              className="text-white text-xs mb-1 text-right "
              style={{ fontStyle: "italic" }}
            >
              г. Воронеж, ул. Профессора Попова, д. 23, БЦ «Технопарк»
            </Text>
          </View>

          <View className={"flex flex-row gap-3"}>
            <Text className="text-white text-xs underline">Телефон:</Text>
            <Text className="text-white text-xs mb-1 text-right">
              +7 (812) 123-45-67
            </Text>
          </View>

          <View className={"flex flex-row gap-3"}>
            <Text className="text-white text-xs underline">
              Электронная почта:
            </Text>
            <Text className="text-white text-xs mb-1 text-right">
              support@streamera.ru
            </Text>
          </View>
        </View>
      </View>

      <View className="border-t border-gray-700 mt-4 pt-2">
        <Text className="text-white text-[11px] text-center">
          © 2025 StreamEra. Все права защищены.
        </Text>
        <Text className="text-white text-[10px] text-center underline">
          Политика конфиденциальности | Пользовательское соглашение
        </Text>
      </View>
    </View>
  );
};

export default Footer;
