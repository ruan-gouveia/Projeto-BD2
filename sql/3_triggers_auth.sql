-- Captura o usuário criado no schema "auth" e o reflete no nosso schema "public"
CREATE OR REPLACE FUNCTION public.cadastrar_usuario_automaticamente()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO public.tb_usuario (id_usuario, email, nome, cargo)
VALUES (
           new.id,
           new.email,
           COALESCE(new.raw_user_meta_data->>'nome', 'Novo Usuário'),
           'CLIENTE'
       );
RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Fica "escutando" a tabela auth.users e dispara a função acima a cada novo cadastro
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;

CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE PROCEDURE public.cadastrar_usuario_automaticamente();