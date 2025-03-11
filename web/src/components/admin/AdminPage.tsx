import useFormAddCreatorStore from "@/src/state/formAddCreatorState";
import useFormAddFilmStore from "@/src/state/formAddFilmState";

import { View } from "react-native";
import FormAddCreator from "./form-add-creator/FormAddCreator";
import FormAddFilm from "./form-add-film/FormAddFilm";

const AdminPage = (): React.ReactElement => {
  const isFormAddCreatorVisible = useFormAddCreatorStore(
    (state) => state.isFormAddCreatorVisible,
  );
  const isFormAddFilmVisible = useFormAddFilmStore(
    (state) => state.isFormAddFilmVisible,
  );

  return (
    <View>
       {isFormAddFilmVisible && <FormAddFilm />}
       {isFormAddCreatorVisible && <FormAddCreator />}
       {/*<FormRelatedShowsChooser/>*/}
    </View>
  );
};

export default AdminPage;
