import useFormAddCreatorStore from "@/src/state/formAddCreatorState";
import useFormAddFilmStore from "@/src/state/formAddFilmState";

import FormAddFilm from "./form-add-film/FormAddFilm";
import FormAddCreator from "./form-add-creator/FormAddCreator";
import { View } from "react-native";

const AdminPage = (): React.ReactElement => {
  const isFormAddFilmVisible = useFormAddCreatorStore(
    (state) => state.isFormAddCreatorVisible
  );
  const isFormAddCreatorVisible = useFormAddFilmStore(
    (state) => state.isFormAddFilmVisible
  );

  return (
    <View>
      {isFormAddFilmVisible && <FormAddFilm />}
      {isFormAddCreatorVisible && <FormAddCreator />}
    </View>
  );
};

export default AdminPage;
